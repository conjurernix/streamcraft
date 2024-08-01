(ns streamcraft.utils.test.persistence
  (:require [clojure.test :refer [is testing]]
            [potpuri.core :as pt]
            [streamcraft.entity.api :as entity]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]))

(def schemas
  {:person
   (entity/as-entity [:map {::entity/name :person
                            ::entity/id   :person/id}
                      [:person/id :uuid]
                      [:person/first-name :string]
                      [:person/last-name :string]
                      [:person/age :int]])})

(defn persistence-fetch-tests []
  (let [{:keys [persistence registry]} *system*]
    (testing "Inserting and retrieving an entity"
      (let [person (->> :person
                        (er/generate registry)
                        (persistence/prepare persistence :person))
            person-id (er/entity-id registry :person person)]
        ; TODO: Fix blocking forever for XTDBv2
        (persistence/persist! persistence :person person)
        (is (= person
               (-> persistence
                   (persistence/fetch :person person-id)
                   (->> (er/select-entity-keys registry :person)))))))
    (testing "Fetching non-existent person should throw"
      (let [random-id (random-uuid)]
        (is (= {:msg  "Entity not found"
                :data {:schema  :person
                       :id-attr :person/id
                       :id      random-id}}
               (catch-thrown-info (persistence/fetch persistence :person random-id))))))))

(defn persistence-search-tests []
  (let [{:keys [persistence registry]} *system*]
    (testing "Inserting multiple entities and searching for them"
      (let [persons (for [person [{:person/first-name "John"
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
                      (persistence/prepare persistence :person person))]

        (doseq [person persons]
          (persistence/persist! persistence :person person))

        (testing "Searching for all persons should return all person entities"
          (is (= persons
                 (-> persistence
                     (persistence/search :person)
                     (->> (map #(er/select-entity-keys registry :person %)))))))

        (testing "Searching for all persons but requesting only first name should return all person entities' first-name"
          (is (= (->> persons (map #(select-keys % [:person/first-name])))
                 (-> persistence
                     (persistence/search :person {:keys [:person/first-name]})
                     (->> (map #(er/select-entity-keys registry :person %)))))))

        (testing "Searching for persons with first name John should return 2 person entities"
          (is (= (->> persons
                      (filter #(= (:person/first-name %) "John")))
                 (-> persistence
                     (persistence/search :person {:where {:person/first-name "John"}})
                     (->> (map #(er/select-entity-keys registry :person %)))))))

        (testing "Searching for persons with age 30 should return 1 person entity"
          (is (= (->> persons (filter (pt/where-fn {:person/age 30})))

                 (-> persistence
                     (persistence/search :person {:where {:person/age 30}})
                     (->> (map #(er/select-entity-keys registry :person %)))))))

        (testing "Searching for persons with age 30 and first name John should return 1 person entity"
          (is (= (->> persons (filter
                                #(and (= (:person/age %) 30)
                                      (= (:person/first-name %) "John"))))
                 (-> persistence
                     (persistence/search :person {:where {:person/age 30 :person/first-name "John"}})
                     (->> (map #(er/select-entity-keys registry :person %)))))))

        (testing "Searching for persons with first name filter and requesting only the key :last-name John should return only
        the last name of the persons with first name John"
          (is (= (->> persons
                      (filter #(= (:person/first-name %) "John"))
                      (map #(select-keys % [:person/last-name])))
                 (-> persistence
                     (persistence/search :person {:keys  [:person/last-name]
                                                  :where {:person/first-name "John"}})
                     (->> (map #(er/select-entity-keys registry :person %)))))))))))
