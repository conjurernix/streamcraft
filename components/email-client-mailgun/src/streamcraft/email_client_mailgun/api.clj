(ns streamcraft.email-client-mailgun.api
  (:require [streamcraft.email-client-mailgun.core :as core]))

(defn make-email-client []
  (core/make-email-client))
