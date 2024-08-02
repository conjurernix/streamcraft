(ns streamcraft.entity-manager.core-test
  (:require [clojure.test :refer [deftest testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.entity-manager-test :refer :all]
            [streamcraft.utils.test :refer :all]))

(def schemas
  {:person
   [:map {::em/name "Person"
          ::em/key  :person
          ::em/id   :person/id}
    [:person/id :uuid]
    [:person/first-name :string]
    [:person/last-name :string]
    [:person/age :int]]
   :vector-of-ints [:vector :int]
   :name           :string})

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :config {:uri "datomic:mem://test"}
                        :schemas schemas
                        :entity-manager (component/using
                                          (fresh-entity-manager)
                                          [:schemas]))))

(deftest validate--test
  (testing "MalliEntityManager/validate test."
    (entity-manager-validate-test)))

(deftest generate--test
  (testing "MalliEntityManager/generate test."
    (entity-manager-generate-test)))

(deftest name--test
  (testing "MalliEntityManager/name test."
    (entity-manager-name-test)))

(deftest properties--test
  (testing "MalliEntityManager/properties test."
    (entity-manager-properties-test)))

(deftest of-type--test
  (testing "MalliEntityManager/of-type test."
    (entity-manager-of-type-test :string :int)))

(deftest cardinality--test
  (testing "MalliEntityManager/cardinality test."
    (entity-manager-cardinality-test)))

(deftest entity?--test
  (testing "MalliEntityManager/entity test."
    (entity-manager-entity?-test)))

(deftest entity-id--key
  (testing "MalliEntityManager/entity-id test."
    (entity-manager-entity-id-key)))

(deftest entity-id--test
  (testing "MalliEntityManager/entity-id test."
    (entity-manager-entity-id-test)))

(deftest select-entity-keys--test
  (testing "MalliEntityManager/select-entity-keys test."
    (entity-manager-select-entity-keys-test)))
