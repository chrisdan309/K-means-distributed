from tcpclient import TcpClient
from KMeansAlgorihm import KMeansAlgorithm
from Point import Point

class Client:
    def __init__(self):
        self.sums = [0.0] * 40
        self.tcp_client = None

    @staticmethod
    def main():
        client = Client()
        client.start()

    def start(self):
        self.tcp_client = TcpClient("127.0.0.1", self.receive_from_client)
        self.tcp_client.run()

    def receive_from_client(self, input_data):

        print("Mensaje recibido: " + input_data)
        parts = input_data.split("/")
        vector_message = parts[0]
        centroid_message = parts[1]


        vector_parts = vector_message.split(" ")
        points = self.split_points(vector_parts)

        centroid_parts = centroid_message.split(" ")
        centroids = self.split_points(centroid_parts)

        for point in points:
            print(point)

        for centroid in centroids:
            print(centroid)

        self.process(points, centroids)

    def split_points(self, vector_parts):
        num_vectors = len(vector_parts) - 2
        puntos = []
        for i in range(2, num_vectors + 2):
            vector_part = vector_parts[i].split("(")
            name = vector_part[0].strip()
            #print(name)
            coordinates = vector_part[1].split(")")
            #print(coordinates)
            coordinates_parts = coordinates[0].split(",")
            #print(coordinates_parts)
            x = float(coordinates_parts[0].strip())
            y = float(coordinates_parts[1].strip())
            #print(x)
            #print(y)
            #print(name)
            punto = Point(y, x, name)
            #print(punto)
            puntos.append(punto)
            #print("avervvevr", puntos)
        
        #print("unu",puntos)

        return puntos


    def send_to_client(self, message):
        if self.tcp_client is not None:
            self.tcp_client.send_message(message)

    def process(self, points, centroids):
        k_means_algorithm = KMeansAlgorithm(points, centroids)
        k_means_algorithm.assign_points()
        k_means_algorithm.update_centroids()

        message = "Resultado vector "
        for point in points:
            message += f"{point.name}({point.y:.1f},{point.x:.1f})-{point.cluster}, "
        message += "/Resultado centroide "
        for centroid in centroids:
            message += f"{centroid.name}({centroid.y:.1f},{centroid.x:.1f})-{centroid.puntos}, "

        print(message)
        print("-----------------------------------------")
        self.send_to_client(message)


if __name__ == "__main__":
    Client.main()