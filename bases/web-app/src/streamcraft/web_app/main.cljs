(ns ^:dev/always streamcraft.web-app.main)

(defonce reactor nil)

(defn start! [client-entrypoint]
  (set! reactor (client-entrypoint
                  #(js/console.log "Reactor success:" %)
                  #(js/console.error "Reactor failure:" %))))

(defn stop! []
  (when reactor (reactor))                                  ; teardown
  (set! reactor nil))