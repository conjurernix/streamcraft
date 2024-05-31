(ns streamcraft.repl.core
  (:require [taoensso.timbre :as log]
            [nrepl.server :as nrepl]))

(defn start-nrepl!
  "Start a nREPL for debugging on specified port."
  [nrepl]
  (try
    (let [{:keys [port] :as server} (nrepl/start-server nrepl)]
      (log/info "Starting nREPL server on port" port)
      (spit ".nrepl-port" port)
      server)

    (catch Throwable t
      (log/error t "Failed to start nREPL")
      (throw t))))


