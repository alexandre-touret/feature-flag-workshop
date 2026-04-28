import http from 'k6/http';
import {check} from 'k6';

export const options = {
    vus: 1, // number of virtual users
    iterations: 20, // number of iterations
};

export default function () {
    // We use __ITER to generate a unique user identifier for each iteration
    const userIndex = __ITER + 1;
    const userEmail = `user${userIndex}@musician.com`;

    const url = 'http://localhost:8080/instruments';

    const params = {
        headers: {
            'User': JSON.stringify({
                firstName: 'test',
                lastName: `user${userIndex}`,
                email: userEmail,
                country: 'UK'
            }),
            'Accept': 'application/json',
        },
    };

    const res = http.get(url, params);

    // Check if the request was successful
    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    // Extract the response body
    let hasDiscount = false;

    try {
        const body = res.json();
        if (Array.isArray(body)) {
            hasDiscount = body.some(instrument => instrument.hasDiscount !== undefined && instrument.hasDiscount !== null && instrument.hasDiscount == true);
        } else {
            hasDiscount = body.hasDiscount !== undefined && body.hasDiscount == true;
        }
    } catch (e) {
        console.error(`Failed to parse response for user ${userEmail}`);
    }

    // Print the result to the console for this iteration
    console.log(`User: ${userEmail} -> has discount (hasDiscount present): ${hasDiscount}`);
}
