import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 100 }, // below normal load
        { duration: '1m', target: 100 },
        { duration: '10s', target: 400 }, // spike to 1400 users
        { duration: '3m', target: 400 }, // stay at 1400 for 3 minutes
        { duration: '10s', target: 100 }, // scale down. Recovery stage.
        { duration: '3m', target: 100 },
        { duration: '10s', target: 0 },
    ],
};
export default function () {
    const BASE_URL = 'http://host.docker.internal:8080/request?latencies='; // make sure this is not production

    const responses = http.batch([
        ['GET', `${BASE_URL}0,0`, null, { tags: { name: '0,0' } }],
        ['GET', `${BASE_URL}100,500`, null, { tags: { name: '100, 500' } }],
        // ['GET', `${BASE_URL}/public/crocodiles/3/`, null, { tags: { name: 'PublicCrocs' } }],
        // ['GET', `${BASE_URL}/public/crocodiles/4/`, null, { tags: { name: 'PublicCrocs' } }],
    ]);

    sleep(1);
}
