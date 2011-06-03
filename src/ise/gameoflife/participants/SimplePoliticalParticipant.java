/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.participants;

/**
 *
 * @author harry
 */
public class SimplePoliticalParticipant {
        public float social;
        public float economic;

        public SimplePoliticalParticipant()
        {
                this.social = 0;
                this.economic = 0;
        }

        public SimplePoliticalParticipant(float social, float economic)
        {
                this.social = social;
                this.economic = economic;
        }
}
