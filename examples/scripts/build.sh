#!/usr/bin/env bash

function build_demos() {
    echo "Building cljs app..."
    lein cljsbuild once demos
    echo "Finished building cljs app..."
}


build_demos
