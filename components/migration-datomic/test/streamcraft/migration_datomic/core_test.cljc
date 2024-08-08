(ns streamcraft.migration-datomic.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity-manager.api :as-alias entity]
            [streamcraft.entity-manager.core-test :refer [schemas]]
            [streamcraft.protocols.api.migration :as migration]
            [streamcraft.utils.test :refer :all]))

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :obs-config {:publishers [{:type :console}]}
                        :obs (component/using
                               (fresh-mulog-observability)
                               {:config :obs-config})
                        :schemas schemas
                        :entity-manager (component/using
                                          (fresh-entity-manager)
                                          [:obs :schemas])
                        :persistence-transformer
                        (component/using
                          (fresh-malli-datomic-persistence-schema-transformer)
                          [:obs :entity-manager])
                        :migration (component/using
                                     (fresh-datomic-migration)
                                     [:obs :entity-manager :persistence-transformer]))))

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
