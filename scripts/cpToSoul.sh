#!/bin/sh

pushd target >> /dev/null
scp target/original-percentage-apbt-1.0.jar target/percentage-apbt-1.0.jar soul:~/tibet/lib/
popd >> /dev/null
