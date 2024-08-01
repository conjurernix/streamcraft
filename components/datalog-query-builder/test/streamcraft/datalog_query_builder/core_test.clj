(ns streamcraft.datalog-query-builder.core-test
  (:require [clojure.test :refer :all]
            [streamcraft.datalog-query-builder.core :as sut]))

(deftest keyword->datalog-query-symbol-test
  (testing "when the keyword is not namespaced, the symbol should be prefixed with a question mark"
    (is (= '?name (sut/keyword->datalog-query-symbol :name)))
    (is (= '?key (sut/keyword->datalog-query-symbol :key))))
  (testing "when the keyword is namespaced, the symbol should be prefixed with a question mark followed by the namespace"
    (is (= '?ns-name (sut/keyword->datalog-query-symbol :ns/name)))
    (is (= '?ns-key (sut/keyword->datalog-query-symbol :ns/key)))))

(deftest build-clauses-test
  (testing "when input is valid, a map with :in and :where entries
            should be returned that matches the structure of a datalog query"
    (is (= '{:in    [?person-id ?person-name]
             :where [[?e :person/id ?person-id]
                     [?e :person/name ?person-name]]}
           (sut/build-clauses {:person/id   :john
                               :person/name "John"}))))
  (testing "when the entity symbol is overridden by the caller, the query should use the new symbol"
    (is (= '{:in    [?person-id ?person-name]
             :where [[?person :person/id ?person-id]
                     [?person :person/name ?person-name]]}
           (sut/build-clauses {:person/id   :john
                               :person/name "John"}
                              '?person)))))

(deftest build-query-test
  (testing "when input is valid, a query that can be used with datalog should be returned"
    (is (= {:query '{:find  [(pull ?e [*])]
                     :in    [$ ?person-name]
                     :where [[?e :person/id _]
                             [?e :person/name ?person-name]]}
            :args ["John"]}
           (sut/build-query {:where {:person/name "John"}}
                            :person/id)))
    (is (= {:query '{:find  [(pull ?e [*])]
                     :in    [$ ?person-age]
                     :where [[?e :person/id _]
                             [?e :person/age ?person-age]]}
            :args [30]}
           (sut/build-query {:where {:person/age 30}}
                            :person/id))))
  (testing "when the :keys key is provided, the query should use the provided keys"
    (is (= {:query '{:find  [(pull ?e [:person/id :person/name])]
                     :in    [$ ?person-name]
                     :where [[?e :person/id _]
                             [?e :person/name ?person-name]]}
            :args  ["John"]}
           (sut/build-query {:keys  [:person/id :person/name]
                             :where {:person/name "John"}}
                            :person/id)))))


