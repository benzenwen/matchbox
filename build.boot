(set-env!
 :resource-paths #{"src"}
 :dependencies '[[adzerk/boot-cljs      "0.0-3308-0" :scope "test"]
                 [adzerk/boot-cljs-repl "0.1.10-SNAPSHOT"      :scope "test"]
                 [adzerk/boot-reload    "0.3.1"      :scope "test"]
                 [adzerk/boot-test      "1.0.4"      :scope "test"]
                 [adzerk/bootlaces      "0.1.11"              :scope "test"]
                 [pandeiro/boot-http    "0.6.3-SNAPSHOT"      :scope "test"]

                 [crisptrutski/boot-cljs-test "0.1.0-SNAPSHOT" :scope "test"]

                 [org.clojure/tools.nrepl "0.2.10" :scope "provided"]

                 [org.clojure/clojurescript        "0.0-3308" :scope "provided"]
                 [org.clojure/core.async           "0.1.346.0-17112a-alpha"]
                 [cljsjs/firebase                  "2.2.7-0"]
                 [com.firebase/firebase-client-jvm "2.3.1"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0-SNAPSHOT")

(task-options!
  pom {:project     'crisptrutski/matchbox
       :version     +version+
       :description "Use Firebase with flair from Clojure and Clojurescript"
       :url         "https://github.com/crisptrutski/matchbox"
       :scm         {:url "https://github.com/crisptrutski/matchbox"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[adzerk.boot-test      :refer :all]
 '[boot.pod              :refer [make-pod]]
 '[pandeiro.boot-http    :refer [serve]]

 '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(deftask build-android-stub []
  (set-env! :src-paths nil
            :resource-paths #{"lib/android-context"}
            :dependencies [])
  (comp
    (pom :project     'crisptrutski/android-context-stub
         :version     "0.0.1"
         :description "Workaround for https://groups.google.com/forum/#!searchin/firebase-talk/jvm/firebase-talk/XLbpLpqCdDI/mbXk1AMmOY8J"
         :url         "https://github.com/crisptrutski/matchbox/lib/android-context"
         :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"})
    (javac)
    (jar)
    (install)))

(defn add-android-stub []
  (set-env! :dependencies #(conj % '[crisptrutski/android-context-stub "0.0.1"])))

(deftask dev []
  (set-env! :source-paths (constantly #{"src" "test"}))
  (add-android-stub)
  (comp
    (serve :dir "target/")
    (watch)
    (speak)
    (reload)
    (cljs-repl)
    (cljs :source-map true :optimizations :none)))

(deftask testing []
  (add-android-stub)
  (set-env! :source-paths #(conj % "test"))
  identity)

(deftask autotest []
  (comp
   (testing)
   (watch)
   (test)
   (test-cljs)))
