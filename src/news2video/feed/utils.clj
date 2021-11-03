(ns news2video.feed.utils
  (:require [clj-http.client :as client]
            [news2video.utils :as utils]))

(defn img-url->image!* [{{:keys [url]} :image :as new-entry}]
  (let [req-opts {:as :stream :insecure? true}
        response (-> url (client/get req-opts))
        image-data (:body response)
        image-filename (format "/tmp/%s.jpg" (utils/rand-str))]
    (utils/data->disk! image-data image-filename)
    (-> new-entry
        (assoc-in [:image :filename] image-filename))))

(def img-url->image! (memoize img-url->image!*))

