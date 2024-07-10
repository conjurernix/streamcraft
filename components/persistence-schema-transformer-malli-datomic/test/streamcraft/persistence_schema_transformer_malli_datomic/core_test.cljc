(ns streamcraft.persistence-schema-transformer-malli-datomic.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.transformer.schema :as ts]
            [streamcraft.utils.test :refer :all]))

(def schemas {:simple-person [:map {:entity/name :simple-person}
                              [:simple-person/name :string]
                              [:simple-person/age :int]
                              [:simple-person/email [:re #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"]]
                              [:simple-person/active :boolean]]})

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :schemas schemas
                        :registry (component/using
                                    (fresh-entity-registry)
                                    [:schemas])
                        :transformer (component/using
                                       (fresh-malli-datomic-persistence-schema-transformer)
                                       [:registry]))))

(deftest transform--test
  (testing "Transforming a simple schema to Datomic schema"
    (let [{:keys [transformer]} *system*]
      (is (= [{:db/ident       :simple-person/name
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one
               :db/fulltext    true}
              {:db/ident       :simple-person/age
               :db/valueType   :db.type/long
               :db/cardinality :db.cardinality/one}
              {:db/ident       :simple-person/email
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one
               :db/fulltext    true}
              {:db/ident       :simple-person/active
               :db/valueType   :db.type/boolean
               :db/cardinality :db.cardinality/one}]
             (ts/transform transformer :simple-person))))))
