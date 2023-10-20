class KMeansAlgorithm:
    def __init__(self, data, centroides):
        self.data = data
        self.centroides = centroides

    def assign_points(self):
        for point in self.data:
            min_distance = float('inf')
            cluster = -1
            for i in range(len(self.centroides)):
                distance = self.calculate_distance(point, self.centroides[i])
                if distance < min_distance:
                    min_distance = distance
                    cluster = i
            self.centroides[cluster].puntos += 1
            if point.cluster != cluster:
                point.cluster = cluster

    def update_centroids(self):
        for i in range(len(self.centroides)):
            sum_x = 0
            sum_y = 0
            count = 0
            for point in self.data:
                if point.cluster == i:
                    sum_x += point.x
                    sum_y += point.y
                    count += 1
            if count > 0:
                self.centroides[i].x = sum_x / count
                self.centroides[i].y = sum_y / count

    @staticmethod
    def calculate_distance(a, b):
        return ((a.x - b.x) ** 2 + (a.y - b.y) ** 2) ** 0.5
