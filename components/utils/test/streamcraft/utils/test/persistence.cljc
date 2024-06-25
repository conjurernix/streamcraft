(ns streamcraft.utils.test.persistence
  (:require [clojure.test :refer [is testing]]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]))

(defn persistence-fetch-tests []
  (let [{:keys [persistence]} *system*
        person-id (random-uuid)
        person (-> :person
                   (gen-entity)
                   (->> (persistence/prepare persistence :person)))]
    ; TODO: Fix blocking forever for XTDBv2
    (persistence/persist! persistence :person person)
    (testing "Inserted random person should be fetch-able"
      (is (= [person]
             (-> persistence
                 (persistence/fetch :person person-id)))))
    (testing "Fetching non-existent person should throw"
      (is (= {:message "Entity not found"
              :data    {:schema :person :id person-id}}
             (catch-thrown-info (-> persistence
                                    (persistence/fetch :person (random-uuid)))))))))
