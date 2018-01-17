# pizza-delivery-spring-boot

Preuve de concept BPMn avec Camunda.

Technologies :
- Camunda
- Spring boot


Le modèle global est constitué de deux processus :
- commande
- livraison

Les deux processus communiquent entre eux via activemq.
![alt text](https://github.com/frederik78/pizza-delivery-spring-boot/pizza-order.png)

A ceci est associé le moteur de règles métier DMN afin de déterminer le livreur le plus approprié en fonction du code postal de la commande.

