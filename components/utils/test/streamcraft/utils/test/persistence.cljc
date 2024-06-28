(ns streamcraft.utils.test.persistence
  (:require [clojure.test :refer [is testing]]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]))

(defn persistence-fetch-tests []
  (let [{:keys [persistence registry]} *system*]
    (testing "Inserting and retrieving an entity"
      (let [
            person-id (random-uuid)
            person (->> :person
                        (er/generate registry)
                        (persistence/prepare persistence :person))]
        ; TODO: Fix blocking forever for XTDBv2
        (persistence/persist! persistence :person person)
        (is (= [person]
               (-> persistence
                   (persistence/fetch :person person-id))))))
    (testing "Fetching non-existent person should throw"
      (let [random-id (random-uuid)]
        (is (= {:message "Entity not found"
                :data    {:schema :person :id random-id}}
               (catch-thrown-info (-> persistence
                                      (persistence/fetch :person random-id)))))))))
