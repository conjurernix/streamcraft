(ns streamcraft.protocols.api.persistence-test
  (:require [clojure.test :refer [is testing]]
            [potpuri.core :as pt]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]))

(defn persistence-prepare-test []
  (let [{:keys [persistence]} *system*]
    (testing "Preparing an entity should validate against its schema"
      (testing "Preparing a valid person should return the person"
        (let [valid-person #:person{:id         (random-uuid)
                                    :first-name "John"
                                    :last-name  "Doe"
                                    :age        21}]
          (is (= valid-person
                 (persistence/prepare persistence :person valid-person)))))
      (testing "Preparing an invalid person should throw an exception"
        (let [invalid-person #:person{:id         (random-uuid)
                                      :first-name "John"
                                      :last-name  "Doe"
                                      :age        "Invalid age"}]
          (is (= {:message "Data is not a valid Person"
                  :data    {:entity-schema :person
                            :errors        {:error-reason {:person/age ["should be an integer"]}
                                            :error-value  {:person/age "Invalid age"}}}}
                 (catch-thrown-info (persistence/prepare persistence :person invalid-person)))))))))

(defn persistence-persist!-test []
  (let [{:keys [persistence entity-manager]} *system*]
    (testing "Persisting an entity should return the entity with the id"
      (let [person #:person{:id         (random-uuid)
                            :first-name "John"
                            :last-name  "Doe"
                            :age        21}]
        (is (= person
               (->> (persistence/persist! persistence :person person)
                    (em/select-entity-keys entity-manager :person))))))))

(defn persistence-patch!-test []
  (let [{:keys [persistence entity-manager]} *system*]
    (testing "Patching an entity should return the patched entity"
      (let [person #:person{:id         (random-uuid)
                            :first-name "John"
                            :last-name  "Doe"
                            :age        21}
            {:keys [person/id]} (persistence/persist! persistence :person person)]

        (is (= (assoc person :person/first-name "Jane")
               (->> (persistence/patch! persistence :person id
                                        {:person/first-name "Jane"})
                    (em/select-entity-keys entity-manager :person))))))))

(defn persistence-delete!-test []
  (let [{:keys [persistence entity-manager]} *system*]
    (testing "Patching an entity should return the patched entity"
      (let [person #:person{:id         (random-uuid)
                            :first-name "John"
                            :last-name  "Doe"
                            :age        21}
            {:keys [person/id]} (persistence/persist! persistence :person person)]



        (is (= person
               (->> (persistence/fetch persistence :person id)
                    (em/select-entity-keys entity-manager :person))))

        (persistence/delete! persistence :person id)

        (is (= {:message "Entity not found"
                :data    {:schema  :person
                          :id-attr :person/id
                          :id      id}}
               (catch-thrown-info (persistence/fetch persistence :person id))))))))

(defn persistence-fetch-tests []
  (let [{:keys [persistence entity-manager]} *system*]
    (testing "Inserting and retrieving an entity"
      (let [person (em/generate entity-manager :person)
            person-id (em/entity-id entity-manager :person person)]
        ; TODO: Fix blocking forever for XTDBv2
        (persistence/persist! persistence :person person)
        (is (= person
               (->> (persistence/fetch persistence :person person-id)
                    (em/select-entity-keys entity-manager :person))))))
    (testing "Fetching non-existent person should throw"
      (let [random-id (random-uuid)]
        (is (= {:message "Entity not found"
                :data    {:schema  :person
                          :id-attr :person/id
                          :id      random-id}}
               (catch-thrown-info (persistence/fetch persistence :person random-id))))))))

(defn persistence-search-tests []
  (let [{:keys [persistence entity-manager]} *system*]
    (testing "Inserting multiple entities and searching for them"
      (let [persons [{:person/first-name "John"
                      :person/last-name  "Doe"
                      :person/age        30
                      :person/id         (random-uuid)}
                     {:person/first-name "Jane"
                      :person/last-name  "Doe"
                      :person/age        25
                      :person/id         (random-uuid)}
                     {:person/first-name "John"
                      :person/last-name  "Smith"
                      :person/age        35
                      :person/id         (random-uuid)}]]

        (doseq [person persons]
          (persistence/persist! persistence :person person))

        (testing "Searching for all persons should return all person entities"
          (is (= persons
                 (-> persistence
                     (persistence/search :person)
                     (->> (map #(em/select-entity-keys entity-manager :person %)))))))

        (testing "Searching for all persons but requesting only first name should return all person entities' first-name"
          (is (= (->> persons (map #(select-keys % [:person/first-name])))
                 (-> persistence
                     (persistence/search :person {:keys [:person/first-name]})
                     (->> (map #(em/select-entity-keys entity-manager :person %)))))))

        (testing "Searching for persons with first name John should return 2 person entities"
          (is (= (->> persons
                      (filter #(= (:person/first-name %) "John")))
                 (-> persistence
                     (persistence/search :person {:where {:person/first-name "John"}})
                     (->> (map #(em/select-entity-keys entity-manager :person %)))))))

        (testing "Searching for persons with age 30 should return 1 person entity"
          (is (= (->> persons (filter (pt/where-fn {:person/age 30})))

                 (-> persistence
                     (persistence/search :person {:where {:person/age 30}})
                     (->> (map #(em/select-entity-keys entity-manager :person %)))))))

        (testing "Searching for persons with age 30 and first name John should return 1 person entity"
          (is (= (->> persons (filter
                                #(and (= (:person/age %) 30)
                                      (= (:person/first-name %) "John"))))
                 (-> persistence
                     (persistence/search :person {:where {:person/age 30 :person/first-name "John"}})
                     (->> (map #(em/select-entity-keys entity-manager :person %)))))))

        (testing "Searching for persons with first name filter and requesting only the key :last-name John should return only
        the last name of the persons with first name John"
          (is (= (->> persons
                      (filter #(= (:person/first-name %) "John"))
                      (map #(select-keys % [:person/last-name])))
                 (-> persistence
                     (persistence/search :person {:keys  [:person/last-name]
                                                  :where {:person/first-name "John"}})
                     (->> (map #(em/select-entity-keys entity-manager :person %)))))))))))

(defn persistence-transact!-test []
  (let [{:keys [persistence entity-manager]} *system*
        persons (repeatedly 10 #(em/generate entity-manager :person))]
    (testing "Transacting should persist all the entities in the transaction"
      (->> persons
           (reduce (fn [acc person]
                     (persistence/persist acc :person person))
                   persistence)
           (persistence/transact!))
      (let [persisted (-> persistence
                          (persistence/search :person)
                          (->> (map #(em/select-entity-keys entity-manager :person %))))]
        (is (= persons persisted))))
    (testing "Transacting should update the entities in the transaction"
      (->> persons
           (reduce (fn [acc {:keys [person/id]}]
                     (persistence/patch acc :person id {:person/age 21}))
                   persistence)
           (persistence/transact!))
      (let [persisted (-> persistence
                          (persistence/search :person)
                          (->> (map #(em/select-entity-keys entity-manager :person %))))]
        (is (= (->> persons
                    (map #(assoc % :person/age 21)))
               persisted))))
    (testing "should delete all entities in the transaction"
      (->> persons
           (reduce (fn [acc {:keys [person/id]}]
                     (persistence/delete acc :person id))
                   persistence)
           (persistence/transact!))
      (let [persisted (-> persistence
                          (persistence/search :person)
                          (->> (map #(em/select-entity-keys entity-manager :person %))))]
        (is (= [] persisted))))))