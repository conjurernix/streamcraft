(ns streamwrite.client-base.app
  (:require [hyperfiddle.electric :as e]
            [streamwrite.client-ui.api :as c.ui]))

(e/defn App [req]
  (c.ui/Main. req))