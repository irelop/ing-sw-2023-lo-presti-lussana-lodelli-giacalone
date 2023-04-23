package it.polimi.ingsw.Client.View;

public class WaitingView extends View{

    @Override
    public void run()
    {
        synchronized (this) {
            try {
                this.wait(100);
            } catch (InterruptedException e) {}

            while (!getStopInteraction()) {
                String wait = " Please wait...";
                System.out.print(wait);

                try {
                    this.wait(500);
                } catch (InterruptedException e) {}

                for (int i=0; i<wait.length(); i++)
                    System.out.print("\010");

                }
            }
        }
}
