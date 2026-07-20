import cv2

# Normalmente la integrada es 0, y Camo (al ser virtual) toma el 1 o el 2
CUAL_CAMARA = 1

cap = cv2.VideoCapture(CUAL_CAMARA,cv2.CAP_MSMF)  # CAP_DSHOW evita el warning de "DirectShow" en Windows

if not cap.isOpened():
    print(f"No se pudo abrir la cámara en el índice {CUAL_CAMARA}. Prueba cambiando el número a 2 o 0.")
else:
    print("¡Conexión exitosa con Camo! Presiona 'q' para cerrar la ventana.")
    
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error al recibir el video. Asegúrate de que Camo Studio esté abierto.")
            break
            
        cv2.imshow('Mi Camara Inalambrica (Camo)', frame)
        
        # Salir con la tecla 'q'
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()