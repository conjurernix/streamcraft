(ns ^:dev/always streamcraft.web-app.app
  (:require [hyperfiddle.electric :as e]))

(defn electric-main [entrypoint]
  (e/boot-client {} entrypoint nil))

(defn start! [reactor entrypoint]
  (assert (nil? @reactor) "reactor already running")
  (let [electric-main (electric-main entrypoint)]
    (reset! reactor (electric-main
                      #(js/console.log "Reactor success:" %)
                      #(js/console.error "Reactor failure:" %)))))

(defn stop! [reactor]
  (when @reactor (@reactor))                                ; teardown
  (reset! reactor nil))