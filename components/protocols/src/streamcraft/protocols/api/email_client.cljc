(ns streamcraft.protocols.api.email-client)

(defprotocol IEmailClient
  (send! [this {:keys [from to subject body]}]
    "Sends an email to the specified recipient. Body must be an HTML string."))