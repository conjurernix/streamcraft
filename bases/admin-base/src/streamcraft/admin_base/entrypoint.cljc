(ns streamcraft.admin-base.entrypoint
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Main [req]
  (e/client
    (binding [dom/node js/document.body]
      (dom/div
        (dom/h1 (dom/text "Hello, from Admin app!"))))))