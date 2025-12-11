(ns readmoi-generator
  "Generate project ReadMe.

  From emacs/CIDER, eval buffer C-c C-k generates an html page and a markdown
  chunk.

  From command line:
  ```bash
  $ lein run -m readmoi-generator
  ```"
  {:no-doc true}
  (:require
   [clojure.string :as str]
   [hiccup2.core :refer [raw]]
   [readmoi.core :refer [*project-group*
                         *project-name*
                         *project-version*
                         *wrap-at*
                         print-form-then-eval]]))


(defn replace-ns-str
  "When a `print-form-then-eval` returns a `[:code <string>]` with the ReadMoi
  ns, replace it with string `replacement`."
  {:UUIDv4 #uuid "3e298518-0b4c-41b2-97c9-868783eccf8f"
   :no-doc true}
  [hiccup-block replacement]
  (update hiccup-block 1 #(str/replace % #"readmoi_generator" replacement)))


;; Note: The namespaces in this usage are filenames, so they contain
;; underscores, not hyphens.

(def replacement-ns-str "example_ns")


(readmoi.core/-main)

