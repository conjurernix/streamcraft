(ns streamcraft.datalog-query-builder.core)

(defn keyword->datalog-query-symbol
  "Given a keyword, return a symbol that can be used in a datalog query.
  The symbol is prefixed with a question mark. Retains the keyword's name and namespace.
  If the keyword is namespaced, the question mark is prepended to the namespace, instead of the name."
  [k]
  (let [n (namespace k)
        n (when-not (nil? n)
            (str "?" n))
        k (if (nil? n)
            (str "?" (name k))
            (name k))]
    (symbol n k)))

(defn build-clauses
  "Given a map of where clauses (each key is a property and each value is a value to match)
   build a map with :in and :where entries that matches the structure of a datalog query."
  ([filters]
   (build-clauses filters '?e))
  ([filters entity-sym]
   (reduce (fn [acc [k v]]
             (let [prop-sym (keyword->datalog-query-symbol k)]
               (cond-> acc
                 (vector? v) (update :in conj [prop-sym '...])
                 (not (vector? v)) (update :in conj prop-sym)
                 (map? v) (do
                            (let [{:keys [in where]} (build-clauses v prop-sym)]
                              (-> acc
                                  (update :in concat in)
                                  (update :where concat where))))
                 :always (update :where conj
                                 [entity-sym k prop-sym]))))
           {:in    []
            :where []}
           filters)))

(defn build-query
  "Given a map of where clauses (each key is a property and each value is a value to match)
  build a query that can be used with datalog."
  [{:keys [ keys where] :as _query-opts} entity-id-key]
  (let [{:keys [in where]} (build-clauses where)
        find-clauses (or (when-let [keys (seq keys)]
                           [(list 'pull '?e (vec keys))])
                         '[(pull ?e [*])])]
    (cond-> `{:find  ~find-clauses
              :in    [~'$]
              :where [[~'?e ~entity-id-key ~'_]]}
      (seq in) (update :in into in)
      (seq where) (update :where into where))))