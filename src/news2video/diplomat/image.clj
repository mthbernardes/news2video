(ns news2video.diplomat.image
  (:require [news2video.utils :as utils]
            [image-resizer.core :as resize]
            [image-resizer.format :as format]
            [clj-http.client :as client]
            [clojure.java.io :as io])
  (:import (java.io File)))

(defn ^:private resize! [^String filename]
  (-> filename
      File.
      (resize/force-resize 1280 720)
      (format/as-file filename)))

(defn ^:private download!* [url]
  (let [req-opts {:as :stream :insecure? true}
        response (-> url (client/get req-opts))
        image-data (:body response)
        image-filename (format "/tmp/%s.jpg" (utils/rand-str))
        _ (utils/data->disk! image-data image-filename)
        resized-image-filename (resize! image-filename)]
    (io/delete-file image-filename)
    resized-image-filename))

(def download! (memoize download!*))