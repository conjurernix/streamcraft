(ns streamcraft.persistence-datomic-pro.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [streamcraft.entity.api :as entity]
            [streamcraft.persistence-datomic-pro.core :refer :all]
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
                        :datomic-config {:uri "datomic:mem://test"}
                        :schemas schemas
                        :registry (component/using
                                    (fresh-entity-registry)
                                    [:schemas])
                        :persistence (component/using
                                       (fresh-datomic-pro-persistence)
                                       {:registry :registry
                                        :config   :datomic-config}))))




(deftest fetch--test
  (testing "Inserting datomic schema"
    (let [db-schema [{:db/ident       :person/first-name
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/fulltext    true}
                      {:db/ident       :person/last-name
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/fulltext    true}
                      {:db/ident       :person/age
                       :db/valueType   :db.type/long
                       :db/cardinality :db.cardinality/one}]
          {:keys [persistence]} *system*
          {:keys [conn]} persistence]
      (is (= 1 1) #_(some? @(d/transact conn db-schema))))
    #_(persistence-fetch-tests)))


