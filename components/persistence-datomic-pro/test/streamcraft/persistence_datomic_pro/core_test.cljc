(ns streamcraft.persistence-datomic-pro.core-test
  (:require [clojure.test :refer [deftest testing use-fixtures]]
            [com.stuartsierra.component :as component]
            [streamcraft.entity-manager.core-test :refer [schemas]]
            [streamcraft.persistence-datomic-pro.core :refer :all]
            [streamcraft.protocols.api.persistence-test :refer :all]
            [streamcraft.utils.test :refer :all]))

(use-fixtures :each (with-system-fixture
                      (component/system-map
                        :datomic-config {:uri "datomic:mem://test"}
                        :obs-config {:publishers [{:type :console}]}
                        :obs (component/using
                               (fresh-mulog-observability)
                               {:config :obs-config})
                        :schemas schemas
                        :entity-manager (component/using
                                          (fresh-entity-manager)
                                          [:obs :schemas])
                        :persistence-transformer (component/using
                                                   (fresh-malli-datomic-persistence-schema-transformer)
                                                   [:obs :entity-manager])
                        :migration (component/using
                                     (fresh-datomic-migration)
                                     {:entity-manager          :entity-manager
                                      :persistence-transformer :persistence-transformer
                                      :obs                     :obs})
                        :persistence (component/using
                                       (fresh-datomic-pro-persistence)
                                       {:entity-manager :entity-manager
                                        :config         :datomic-config
                                        :migration      :migration
                                        :obs            :obs}))))


(deftest prepare--test
  (testing "DatomicProPersistence/prepare test."
    (persistence-prepare-test)))

(deftest persist!--test
  (testing "DatomicProPersistence/persist! test."
    (persistence-persist!-test)))

(deftest patch!--test
  (testing "DatomicProPersistence/patch! test."
    (persistence-patch!-test)))

(deftest delete!--test
  (testing "DatomicProPersistence/delete! test."
    (persistence-delete!-test)))

(deftest fetch--test
  (testing "DatomicProPersistence/fetch test."
    (persistence-fetch-tests)))

(deftest search--test
  (testing "DatomicProPersistence/search test."
    (persistence-search-tests)))

(deftest transact!--test
  (testing "DatomicProPersistence/transact! test."
    (persistence-transact!-test)))




