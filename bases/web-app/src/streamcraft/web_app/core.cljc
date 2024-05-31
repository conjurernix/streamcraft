(ns streamcraft.web-app.core
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))


(e/defn App [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (dom/div
        (dom/h1 (dom/text "Hello, world!"))))))