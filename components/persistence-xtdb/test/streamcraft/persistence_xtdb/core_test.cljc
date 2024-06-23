(ns streamcraft.persistence-xtdb.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]
            [xtdb.api :as xt]))

(def registry
  {:person
   [:map {::entity/name :person}
    [:first-name :string]
    [:last-name :string]
    [:age :int]]})

(use-fixtures :once (with-registry registry))

(use-fixtures :each (with-system
                      (component/system-map
                        :config {:xtdb {}}
                        :persistence (component/using
                                       (fresh-xtdb-persistence)
                                       [:config]))))

(deftest fetch--test
  (let [{:keys [persistence]} *system*
        {:keys [node]} persistence
        person-id (random-uuid)
        person (-> :person
                   (gen-entity)
                   (assoc :xt/id person-id))]
    ; TODO: Fix blocking forever
    (xt/execute-tx node [[:put-docs :person person]])
    (testing "Inserted random person should be fetch-able"
      (is (= person
             (-> persistence
                 (persistence/fetch :person person-id)))))))

#_(testing "Trying to fetch using a random-id should throw(?)"
  (is (= {}
         (catch-thrown-info
           (-> persistence
               (persistence/fetch :person (random-uuid)))))))
