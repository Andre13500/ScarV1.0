import cv2


hardcase = cv2.CascadeClassifier('C:/Users/ANDRES/Desktop/ANDRES/PROGRAMACION/Proyectoll/Scar_python/utils/haarcascade_frontalface_default.xml')

video = cv2.VideoCapture(0)
contador = 0
while True:
    ret, frame = video.read()
    if not ret:
        print("Error al capturar el video")
        break

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = hardcase.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)

    for (x, y, w, h) in faces:
        cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)
        rostro_recortado = gray[y:y + h, x:x + w]
        rostro_recortado = cv2.resize(rostro_recortado, (150, 150))
    cv2.imshow('Registro de rostro', frame)

    key = cv2.waitKey(40) & 0xFF



    if key == ord('s') and len(faces)>0:
        while contador < 100:
            ret, frame = video.read()
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = hardcase.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5)
            for (x, y, w, h) in faces:
                    rostro_recortado = gray[y:y + h, x:x + w]
                    rostro_recortado = cv2.resize(rostro_recortado, (150, 150))
                    cv2.imwrite(f'C:/Users/ANDRES/Desktop/ANDRES/PROGRAMACION/Proyectoll/Scar_python/data/Messi/rostro_{contador}.jpg', rostro_recortado)
                    contador += 1
                    print(f"Rostro {contador} guardado correctamente.")
        print("Se han guardado 100 rostros correctamente.")
        break
    elif key == 27:
         break

video.release() 
cv2.destroyAllWindows()