import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    noConnectionReuse: true,
    stages: [
        { duration: '10s', target: 10 }, // below normal load
        { duration: '1m', target: 50 }, // below normal load
        // { duration: '1m', target: 100 }, // below normal load
        // { duration: '1m', target: 100 }, // below normal load
        // { duration: '1m', target: 200 }, // below normal load
        // { duration: '1m', target: 300 }, // below normal load
        // { duration: '1m', target: 400 }, // below normal load
        // { duration: '1m', target: 400 }, // below normal load
        // { duration: '1m', target: 800 }, // below normal load
        // { duration: '1m', target: 800 }, // below normal load
        // { duration: '1m', target: 1200 }, // below normal load
        // { duration: '1m', target: 1200 }, // below normal load
        // { duration: '1m', target: 2000 }, // below normal load
        // { duration: '1m', target: 2000 }, // below normal load
        { duration: '1m', target: 0 }, // scale down. Recovery stage.
    ],
};

export default function () {
    const BASE_URL = `http://${__ENV.THE_IP}:8081/request?latencies=`; // make sure this is not production
    // const BASE_URL = 'http://localhost:8081/request?latencies='; // make sure this is not production
    // const BASE_URL = 'http://host.docker.internal:8081/request?latencies='; // make sure this is not production

    let response = http.get(`${BASE_URL}0,0`);

    if (response.status != 200) {
        console.error('Could not send summary, got status ' + response.status);
    }

    sleep(1);
}

//docker run --rm -i -v "$PWD:/work" grafana/k6 run - <load.js > adamfooagain.txt 2>&1 &
