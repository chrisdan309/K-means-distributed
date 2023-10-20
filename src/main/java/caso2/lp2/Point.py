class Point:
    def __init__(self, x, y, name=""):
        self.name = name
        self.x = x
        self.y = y
        self.cluster = -1
        self.puntos = 0

    @classmethod
    def add(cls, x, y):
        return Point(x.x + y.x, x.y + y.y)

    @classmethod
    def scalar(cls, k, p):
        return Point(k * p.x, k * p.y)

    def __str__(self):
        return f"{self.name}({self.x}, {self.y})"
