(ns streamcraft.migration-datomic.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.utils.test :refer :all]
            [streamcraft.utils.test.persistence :refer :all]))

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :schemas schemas
                        :registry (component/using
                                    (fresh-entity-registry)
                                    [:schemas])
                        :persistence-transformer
                        (component/using
                          (fresh-malli-datomic-persistence-schema-transformer)
                          [:registry])
                        :migration (component/using
                                     (fresh-datomic-migration)
                                     [:registry :persistence-transformer]))))

(deftest gen-migration--test
  (testing "Generating migration"
    (let [{:keys [migration]} *system*]
      (is (= [{:db/ident       :person/id
               :db/valueType   :db.type/uuid
               :db/cardinality :db.cardinality/one}
              {:db/ident       :person/first-name
               :db/cardinality :db.cardinality/one
               :db/valueType   :db.type/string}
              {:db/ident       :person/last-name
               :db/cardinality :db.cardinality/one
               :db/valueType   :db.type/string}
              {:db/ident       :person/age
               :db/cardinality :db.cardinality/one
               :db/valueType   :db.type/long}]
             (migration/gen-migration migration))))))
