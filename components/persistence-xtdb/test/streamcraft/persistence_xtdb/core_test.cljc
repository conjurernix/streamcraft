(ns streamcraft.persistence-xtdb.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as-alias entity]
            [streamcraft.protocols.api.entity-registry :as er]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]
            [streamcraft.utils.test.persistence :refer :all]))

(def registry
  {:person
   [:map {::entity/name :person}
    [:first-name :string]
    [:last-name :string]
    [:age :int]]})

(use-fixtures :each (with-system
                      (component/system-map
                        :xtdb-config {}
                        :registry (doto (fresh-entity-registry)
                                    (er/merge-registry registry))
                        :persistence (component/using
                                       (fresh-xtdb-persistence)
                                       {:registry :registry
                                        :config   :xtdb-config}))))

(deftest fetch--test
  (persistence-fetch-tests))

#_(testing "Trying to fetch using a random-id should throw(?)"
    (is (= {}
           (catch-thrown-info
             (-> persistence
                 (persistence/fetch :person (random-uuid)))))))
