package stp;

public class RunTest {

    public static void main(String[] args) {

        //test case 1: passed
		System.out.println(">>>>>>>>>>>>>>>>test 1 \n\n\n");
		Topologia topo1 = new Topologia("input1.txt");
		topo1.runStp();
		topo1.log("output1.txt");

		//test case 2: passed
		System.out.println(">>>>>>>>>>>>>>>>test 2 \n\n\n");
		Topologia topo2 = new Topologia("input2_loop.txt");
		topo2.runStp();
		topo2.log("output2.txt");

		//test case 3: passed
		System.out.println(">>>>>>>>>>>>>>>>test 3 \n\n\n");
		Topologia topo3 = new Topologia("input3_noLoop.txt");
		topo3.runStp();
		topo3.log("output3.txt");

        //test case 4: passed
		System.out.println(">>>>>>>>>>>>>>>>test 4 \n\n\n");
        Topologia topo4 = new Topologia("input4.txt");
        topo4.runStp();
        topo4.log("output4.txt");

        //test case 5: passed
		System.out.println(">>>>>>>>>>>>>>>>test 5 \n\n\n");
		Topologia topo5 = new Topologia("input5_LoopComplesso.txt");
		topo5.runStp();
		topo5.log("output5.txt");
    }
}
