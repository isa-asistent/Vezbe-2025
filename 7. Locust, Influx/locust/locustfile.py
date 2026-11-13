import random

from locust import HttpUser, task, between

# Zakomentarisati @task-ove koji nisu trenutni endpoint. Ukoliko postoji vise @task u file-u, locust ce ih sve pokrenuti. Ukoliko testirate pojedinacan endpoint, ostavi otkomentarisan samo onaj @task koji koristite za testiranje.

class ProductUser(HttpUser):
    wait_time = between(1, 5)
    host = "http://localhost:8080"

    names = ["phone", "laptop", "shirt", "book", "table", "a", "PHONE", "lap", "does-not-exist", "čaj", "with space"]
    price_ranges = [
        (0, 10),
        (10, 100),
        (100, 1000),
        (0, 100000),
        (50.5, 99.99),
        (500, 100),  # lower > upper
        (-10, 10),   # negative lower bound
    ]

    # ---------- /api/products/byName ----------

    @task
    def by_name_random(self):
        name = random.choice(self.names)
        self.client.get(
            "/api/products/byName",
            params={"name": name},
            name="/api/products/byName [name]",
        )

    @task
    def by_name_all_known(self):
        for name in self.names[:5]:
            self.client.get(
                "/api/products/byName",
                params={"name": name},
                name="/api/products/byName [name]",
            )

    # ---------- /api/products/byPriceRange ----------

    @task
    def by_price_range_random(self):
        lower = round(random.uniform(0, 500), 2)
        upper = round(lower + random.uniform(0, 1000), 2)
        self.client.get(
            "/api/products/byPriceRange",
            params={"lower": lower, "upper": upper},
            name="/api/products/byPriceRange [random]",
        )
    # ---------- helpers ----------

    @staticmethod
    def _accept(resp, ok):
        if resp.status_code in ok:
            resp.success()
        else:
            resp.failure(f"Unexpected status {resp.status_code}")

    def on_start(self):
        print("On start called")

    def on_stop(self):
        print("On stop called")
