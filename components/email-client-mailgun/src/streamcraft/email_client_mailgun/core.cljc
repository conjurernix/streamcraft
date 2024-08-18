(ns streamcraft.email-client-mailgun.core
  (:require [com.stuartsierra.component :as component]
            [mailgun.mail :as mail]
            [streamcraft.protocols.api.email-client :as email]
            [streamcraft.logging.api :as log]))

(defrecord MailgunEmailClient
  [config]

  component/Lifecycle
  (start [this]
    (log/info! :starting-component {:component this})
    this)
  (stop [this]
    (log/info! :stopping-component {:component this})
    this)

  email/IEmailClient
  (send! [_ {:keys [from to subject body]}]
    (let [{:keys [credentials]} config]
      (mail/send-mail credentials {:from    from
                                   :to      to
                                   :subject subject
                                   :body    body}))))

(defn make-email-client []
  (map->MailgunEmailClient {}))
