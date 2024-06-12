(ns ^:dev/always streamcraft.web-app.core
  (:require [hyperfiddle.electric :as e]))

(defn client-entrypoint [entrypoint]
  (e/boot-client {} entrypoint nil))

(defn start! [reactor entrypoint]
  (assert (nil? @reactor) "reactor already running")
  (let [electric-main (client-entrypoint entrypoint)]
    (reset! reactor (electric-main
                      #(js/console.log "Reactor success:" %)
                      #(js/console.error "Reactor failure:" %)))))

(defn stop! [reactor]
  (when @reactor (@reactor))                                ; teardown
  (reset! reactor nil))