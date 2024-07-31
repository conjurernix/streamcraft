(ns streamcraft.persistence-datomic-pro.core-test
  (:require [clojure.test :refer [deftest testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [streamcraft.persistence-datomic-pro.core :refer :all]
            [streamcraft.utils.test :refer :all]
            [streamcraft.utils.test.persistence :refer :all]))



(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :config {:uri "datomic:mem://test"}
                        :schemas schemas
                        :registry (component/using
                                    (fresh-entity-registry)
                                    [:schemas])
                        :persistence-transformer (component/using
                                                   (fresh-malli-datomic-persistence-schema-transformer)
                                                   [:registry])
                        :migration (component/using
                                     (fresh-datomic-migration)
                                     [:registry :persistence-transformer])
                        :persistence (component/using
                                       (fresh-datomic-pro-persistence)
                                       [:registry :config :migration]))))




(deftest fetch--test
  (testing "Datomic Pro Persistence fetch"
    (persistence-fetch-tests)))

(deftest search--test
  (testing "Datomic Pro Persistence search"
    (persistence-search-tests)))




