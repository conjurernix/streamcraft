(ns streamcraft.persistence-schema-transformer-malli-datomic.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.transformer.schema :as ts]
            [streamcraft.utils.test :refer :all]))

(def schemas {:simple-person       [:map {:entity/name :simple-person}
                                    [:simple-person/name :string]
                                    [:simple-person/age :int]
                                    [:simple-person/email [:re #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"]]
                                    [:simple-person/active :boolean]]
              :person-with-friends [:map {:entity/name :person-with-friends}
                                    [:person-with-friends/name :string]
                                    [:person-with-friends/age :int]
                                    [:person-with-friends/email [:re #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"]]
                                    [:person-with-friends/active :boolean]
                                    [:person-with-friends/friends [:vector :string]]]})

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :schemas schemas
                        :obs-config {:publishers [{:type :console}]}
                        :obs (component/using
                               (fresh-mulog-observability)
                               {:config :obs-config})
                        :entity-manager (component/using
                                          (fresh-entity-manager)
                                          [:obs :schemas])
                        :transformer (component/using
                                       (fresh-malli-datomic-persistence-schema-transformer)
                                       [:obs :entity-manager]))))

(deftest transform--test
  (testing "Transforming a simple schema to Datomic schema, with only single cardinality and basic types."
    (let [{:keys [transformer]} *system*]
      (is (= [{:db/ident       :simple-person/name
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :simple-person/age
               :db/valueType   :db.type/long
               :db/cardinality :db.cardinality/one}
              {:db/ident       :simple-person/email
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :simple-person/active
               :db/valueType   :db.type/boolean
               :db/cardinality :db.cardinality/one}]
             (ts/transform transformer :simple-person)))))
  (testing "Transforms a schema to Datomic schema, with multiple cardinality"
    (let [{:keys [transformer]} *system*]
      (is (= [{:db/ident       :person-with-friends/name
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :person-with-friends/age
               :db/valueType   :db.type/long
               :db/cardinality :db.cardinality/one}
              {:db/ident       :person-with-friends/email
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :person-with-friends/active
               :db/valueType   :db.type/boolean
               :db/cardinality :db.cardinality/one}
              {:db/ident       :person-with-friends/friends
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/many}]
             (ts/transform transformer :person-with-friends))))))
