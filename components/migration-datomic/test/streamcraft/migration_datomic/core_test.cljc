(ns streamcraft.migration-datomic.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.utils.test :refer :all]
            [streamcraft.utils.test.persistence :refer :all]))

(def schemas
  {:person
   [:map {::entity/name :person}
    [:person/first-name :string]
    [:person/last-name :string]
    [:person/age :int]]})

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :schemas schemas
                        :registry (component/using
                                    (fresh-entity-registry)
                                    [:schemas])
                        :epst (component/using
                                (fresh-datomic-entity-persistence-schema-transformer)
                                [:registry])
                        :migration (component/using
                                     (fresh-datomic-migration)
                                     [:registry :epst]))))

(deftest gen-migration--test
  (testing "Generating migration"
    (let [{:keys [migration]} *system*]
      (is (= [{:db/ident       :person/first-name
               :db/cardinality :db.cardinality/one
               :db/valueType   :db.type/string}
              {:db/ident       :person/last-name
               :db/cardinality :db.cardinality/one
               :db/valueType   :db.type/string}
              {:db/ident       :person/age
               :db/cardinality :db.cardinality/one
               :db/valueType   :db.type/long}]
             (migration/gen-migration migration))))))
