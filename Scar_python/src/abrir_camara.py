# Borrador: abre la camara y muestra el video hasta presionar ESC.
# Este script es llamado desde Java (CamaraService) mediante ProcessBuilder.
import sys

import cv2


def main():
    video = cv2.VideoCapture(0)

    if not video.isOpened():
        print("No se pudo abrir la camara")
        sys.exit(1)

    while True:
        ret, frame = video.read()
        if not ret:
            print("Error al capturar el video")
            break

        cv2.imshow('SCAR - Camara (ESC para salir)', frame)

        if cv2.waitKey(40) & 0xFF == 27:  # ESC
            break

    video.release()
    cv2.destroyAllWindows()
    sys.exit(0)


if __name__ == "__main__":
    main()
