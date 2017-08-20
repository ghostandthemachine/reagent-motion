(ns ^:figwheel-no-load motion.dev
  (:require
    [motion.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
