(ns streamcraft.utils.api)

(defmacro if-ns
  "Evaluate some code conditionally based on the presence of `ns`."
  [ns then else]
  (if (try
        (require ns)
        true
        (catch java.io.FileNotFoundException _
          false))
    `~then
    `~else))