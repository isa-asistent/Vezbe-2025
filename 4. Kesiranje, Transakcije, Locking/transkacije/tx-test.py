import requests
import random
from concurrent.futures import ThreadPoolExecutor, as_completed

API_URL = "http://localhost:8080/api/products/1"  

PAYLOADS = [{
            "id": 1,            
            "name": "Sample Name",
            "origin": "Sample Origin",
            "price": 1000         
            },
            {
            "id": 1,            
            "name": "Sample Name",
            "origin": "Sample Origin",
            "price": 1003         
            },
            {
            "id": 1,            
            "name": "Sample Name",
            "origin": "Sample Origin",
            "price": 2500         
            },
]  

TIMEOUT = 10

def send_request(idx: int):
    try:
        random_element = random.choice(PAYLOADS)
        if random_element is not None:
            resp = requests.put(API_URL, json=random_element, timeout=TIMEOUT)
        snippet = resp.text[:200]
        print(f"[{idx}] {resp.status_code}: {snippet}")
        return resp.status_code, resp.text
    except requests.Timeout:
        print(f"[{idx}] Timeout")
        return None, None
    except Exception as e:
        print(f"[{idx}] Error: {e}")
        return None, None

def main():
    with ThreadPoolExecutor(max_workers=3) as exe:
        futures = {exe.submit(send_request, i+1): i+1 for i in range(3)}
        results = []
        for fut in as_completed(futures):
            results.append(fut.result())

    print("All done:", results)

if __name__ == "__main__":
    main()
