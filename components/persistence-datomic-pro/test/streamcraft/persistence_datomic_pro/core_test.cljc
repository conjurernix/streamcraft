(ns streamcraft.persistence-datomic-pro.core-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as entity]
            [streamcraft.utils.test :refer :all]
            [streamcraft.utils.test.persistence :refer :all]))

(def schemas
  {:person
   [:map {::entity/name :person}
    [:first-name :string]
    [:last-name :string]
    [:age :int]]})

(use-fixtures :each (with-system
                      (component/system-map
                        :datomic-config {:uri "datomic:mem://test"}
                        :schemas schemas
                        :registry (component/using
                                    (fresh-entity-registry)
                                    [:schemas])
                        :persistence (component/using
                                       (fresh-datomic-pro-persistence)
                                       {:registry :registry
                                        :config   :datomic-config}))))

(deftest fetch--test
  (persistence-fetch-tests))


