import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '1m', target: 50 },
        { duration: '1m', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '1m', target: 200 },
        { duration: '1m', target: 300 },
        { duration: '1m', target: 400 },
        { duration: '1m', target: 400 },
        { duration: '1m', target: 800 },
        { duration: '1m', target: 800 },
        { duration: '1m', target: 1200 },
        { duration: '1m', target: 1200 },
        // { duration: '1m', target: 1600 },
        // { duration: '1m', target: 1600 },
        // { duration: '1m', target: 2000 },
        // { duration: '1m', target: 2000 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(90) < 20000', 'p(95) < 40000', 'p(99.9) < 300000'],
        http_req_failed: ['rate<0.1']
    }
};

export default function () {
    const BASE_URL = `http://${__ENV.THE_IP}:8080/request?latencies=`; // make sure this is not production

    let response = http.get(`${BASE_URL}250,800`);

    if (response.status != 200) {
        console.error('Could not send summary, got status ' + response.status);
    }

    sleep(1);
}
