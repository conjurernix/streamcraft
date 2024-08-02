(ns streamcraft.protocols.api.entity-manager-test
  (:require [clojure.test :refer [is testing]]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.utils.test :refer :all]))

(defn entity-manager-validate-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Validating a valid person should return the person"
      (let [valid-person #:person{:id         (random-uuid)
                                  :first-name "John"
                                  :last-name  "Doe"
                                  :age        21}]
        (is (= valid-person
               (em/validate entity-manager :person valid-person)))))
    (testing "Validating an invalid person should throw an exception"
      (let [invalid-person #:person{:id         (random-uuid)
                                    :first-name "John"
                                    :last-name  "Doe"
                                    :age        "Invalid age"}]
        (is (= {:message "Validation Error"
                :data    {:error-reason {:person/age ["should be an integer"]}
                          :error-value  {:person/age "Invalid age"}}}
               (catch-thrown-info (em/validate entity-manager :person invalid-person))))))))

(defn entity-manager-generate-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Generating data from a person schema should be a conform to the schema"
      (let [generated-person (em/generate entity-manager :person)]
        (is (= generated-person
               (em/validate entity-manager :person generated-person)))))))

(defn entity-manager-name-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return the humanized `name` of the entity schema"
      (is (= "Person"
             (em/name entity-manager :person))))))

(defn entity-manager-properties-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return the properties of the entity schema"
      (is (= {::em/name "Person"
              ::em/key  :person
              ::em/id   :person/id}
             (em/properties entity-manager :person))))))

; TODO: This test couples with `:string` and `:int` of malli
(defn entity-manager-of-type-test
  ;; This is a hack to decouple `:string` and `:int` malli schemas from the generic entity-manager test suite
  [string-schema int-schema]
  (let [{:keys [entity-manager]} *system*]
    (testing "should return the schema if it is not a collection or other composite schema"
      (is (= string-schema (em/of-type entity-manager :name))))
    (testing "should return the first child schema if it is a collection or other composite  schema"
      (is (= int-schema (em/of-type entity-manager :vector-of-ints))))))

(defn entity-manager-cardinality-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return :one for schemas that do not represent a collection of data"
      (is (= :one (em/cardinality entity-manager :name))))
    (testing "Should return :many for schemas that do represent a collection of data"
      (is (= :many (em/cardinality entity-manager :vector-of-ints))))))

(defn entity-manager-entity?-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return true if the schema is an entity schema"
      (is (= true (em/entity? entity-manager :person))))
    (testing "Should return false if the schema is not an entity schema"
      (is (= false (em/entity? entity-manager :name))))))

(defn entity-manager-entity-id-key []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return the id key of the entity schema"
      (is (= :person/id (em/entity-id-key entity-manager :person))))
    (testing "Should throw for a non-entity schema"
      (is (= {:message "Entity schema does not have an id key, or it's not an entity schema"
              :data    {:schema :name}}
             (catch-thrown-info (em/entity-id-key entity-manager :name)))))))

(defn entity-manager-entity-id-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return the id of the entity data based on the entity's id key"
      (let [id (random-uuid)
            person {:person/id         id
                    :person/first-name "John"
                    :person/last-name  "Doe"
                    :person/age        21}]
        (is (= id (em/entity-id entity-manager :person person)))))
    (testing "Should return nil for a non-entity schema"
      (is (= {:message "Entity schema does not have an id key, or it's not an entity schema"
              :data    {:schema :name}}
             (catch-thrown-info (em/entity-id entity-manager :name {})))))))

(defn entity-manager-select-entity-keys-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return the entity data removing any excess keys that are not present in the entity schema"
      (let [person-with-extra-keys {:person/id         (random-uuid)
                                    :person/first-name "John"
                                    :person/last-name  "Doe"
                                    :person/age        21
                                    :extra             :key}]
        (is (= (select-keys person-with-extra-keys [:person/id :person/first-name :person/last-name :person/age])
               (em/select-entity-keys entity-manager :person person-with-extra-keys)))))))

(defn entity-manager-optional-keys-test []
  (let [{:keys [entity-manager]} *system*]
    (testing "Should return a new schema with keys marked as optional, thus partial data can be valid"
      (let [partial-schema (-> entity-manager
                               (em/optional-keys :person))]
        (is (= true
               (em/validate entity-manager partial-schema {:person/id (random-uuid)})))
        (is (= true
               (em/validate entity-manager partial-schema {:person/id         (random-uuid)
                                                           :person/first-name "John"})))
        (is (= false
               (em/validate entity-manager partial-schema {:person/first-name "John"}))))
      (let [partial-schema (-> entity-manager
                               (em/optional-keys :person [:person/first-name :person/last-name]))]
        (is (= false
               (em/validate entity-manager partial-schema {:person/id (random-uuid)})))
        (is (= true
               (em/validate entity-manager partial-schema {:person/first-name "John"
                                                           :person/last-name "Doe"})))
        (is (= true
               (em/validate entity-manager partial-schema {:person/id (random-uuid)
                                                           :person/first-name "John"
                                                           :person/last-name "Doe"})))))))