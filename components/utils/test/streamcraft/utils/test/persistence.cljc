(ns streamcraft.utils.test.persistence
  (:require [clojure.test :refer [is testing]]
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
        (is (= {:msg "Entity not found"
                :data    {:schema :person
                          :id-attr :person/id
                          :id random-id}}
               (catch-thrown-info (persistence/fetch persistence :person random-id))))))))
