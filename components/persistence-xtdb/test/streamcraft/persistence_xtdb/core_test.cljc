(ns streamcraft.persistence-xtdb.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity-manager.api :as-alias entity]
            [streamcraft.protocols.api.entity-manager :as em]
            [streamcraft.protocols.api.persistence :as persistence]
            [streamcraft.utils.test :refer :all]
            [streamcraft.utils.test.persistence :refer :all]))


#_(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :xtdb-config {}
                        :entity-manager (doto (fresh-entity-manager)
                                    (em/merge-registry registry))
                        :persistence (component/using
                                       (fresh-xtdb-persistence)
                                       {:entity-manager :entity-manager
                                        :config   :xtdb-config}))))

(deftest fetch--test
  #_(persistence-fetch-tests))

#_(testing "Trying to fetch using a random-id should throw(?)"
    (is (= {}
           (catch-thrown-info
             (-> persistence
                 (persistence/fetch :person (random-uuid)))))))
