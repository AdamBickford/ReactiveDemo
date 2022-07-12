import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    noConnectionReuse: true,
    stages: [
        { duration: '10s', target: 10 }, // below normal load
        { duration: '1m', target: 50 }, // below normal load
        { duration: '3m', target: 100 }, // below normal load
        { duration: '3m', target: 200 }, // below normal load
        // { duration: '3m', target: 400 }, // below normal load
        // { duration: '3m', target: 800 }, // below normal load
        // { duration: '30s', target: 200 }, // below normal load
        // { duration: '30s', target: 300 }, // below normal load
        // { duration: '30s', target: 200 }, // normal load
        // { duration: '30s', target: 200 },
        // { duration: '30s', target: 400 }, // around the breaking point
        // { duration: '30s', target: 500 },
        // { duration: '30s', target: 400 }, // beyond the breaking point
        // { duration: '30s', target: 400 },
        { duration: '1m', target: 0 }, // scale down. Recovery stage.
    ],
};

export default function () {
    const BASE_URL = 'http://host.docker.internal:8090/request?latencies='; // make sure this is not production

    // http.get(`${BASE_URL}0,0`);

    const responses = http.batch([
        ['GET', `${BASE_URL}0,0`, null, { tags: { name: '0,0' } }],
        // ['GET', `${BASE_URL}0,0`, null, { tags: { name: '10,100' } }],
        // ['GET', `${BASE_URL}100,500`, null, { tags: { name: '0, 50' } }],
        // ['GET', `${BASE_URL}/public/crocodiles/3/`, null, { tags: { name: 'PublicCrocs' } }],
        // ['GET', `${BASE_URL}/public/crocodiles/4/`, null, { tags: { name: 'PublicCrocs' } }],
    ]);
    //
    sleep(0.5);
}

//docker run --rm -i grafana/k6 run - <load.js
