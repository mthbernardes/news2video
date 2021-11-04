(ns news2video.diplomat.tts
  (:require [news2video.utils :as utils]
            [clj-http.client :as client]))

(def ^:private rapid-api-host (or (System/getenv "X_RAPIDAPI_HOST") "voicerss-text-to-speech.p.rapidapi.com"))
(def ^:private rapid-api-key (or (System/getenv "X_RAPIDAPI_KEY") "7jzFJiVjmbmshjaFjg98VXBpEFZWp1lNlLSjsnLET51jBcl2PQ"))
(def ^:private voice-rss-key (or (System/getenv "VOICE_RSS_KEY") "e16c6d545a614dc4acb8a9ac2d70ab45"))

(defn ^:private transform!* [transcript]
  (let [url "https://voicerss-text-to-speech.p.rapidapi.com/"
        req-opts {:headers      {:x-rapidapi-host rapid-api-host
                                 :x-rapidapi-key  rapid-api-key}
                  :query-params {:key voice-rss-key}
                  :as           :stream
                  :form-params  {:src transcript
                                 :hl  "pt-br"
                                 :r   "0"
                                 :v   "Dinis"
                                 :f   "48khz_16bit_stereo"
                                 :c   "mp3"}}
        binary-data (-> url (client/post req-opts) :body)
        output-filename (format "/tmp/%s.mp3" (utils/rand-str))]
    (utils/data->disk! binary-data output-filename)
    output-filename))

(def transform! (memoize transform!*))