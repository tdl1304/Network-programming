use std::thread;
use std::sync::{mpsc, Mutex, Arc};
use std::io::{stdin,stdout,Write};


fn main() {
    let (tx, rx) = mpsc::channel(); //tx = transmitter , rx = receieve
    let c = Arc::new(Mutex::new(0)); //count
    let mut threads = vec![];
    let mut primes: Vec<u32> = vec![];

    let (lim, thread_count) = user_input();
    let range = lim/thread_count;

    for i in 0..thread_count {
        let tx_ = tx.clone();
        threads.push(thread::spawn(move || {
            for j in i*range+1..=(i+1)*range {
                if prime_check(j) {
                    tx_.send(val).unwrap(j); //send message
                }
            }

        }));
    }


    for t in threads {
        t.join().unwrap();
    }

    let mut it = rx.iter();







    //primes.sort_unstable();
    //println!("{}", primes[0]);


}

fn user_input() -> (u32, u32) {
    let mut s1=String::new();
    let mut s2=String::new();
    let mut a: u32 = 0;
    let mut b: u32 = 0;
    println!("Please enter upper limit: ");
    stdin()
        .read_line(&mut s1)
        .expect("Did not enter a correct string");
    println!("Please enter amount of threads");
    stdin()
        .read_line(&mut s2)
        .expect("Did not enter a correct string");
    let trimmed1 = s1.trim();
    let trimmed2 = s2.trim();
    match trimmed1.parse::<u32>() {
        Ok(i) => a = i,
        Err(..) => println!("this was not an integer: {}", trimmed1),
    };
    match trimmed2.parse::<u32>() {
        Ok(i) => b = i,
        Err(..) => println!("this was not an integer: {}", trimmed2),
    };
    (a, b)
}

fn prime_check(n:u32) -> bool {
    let num:u32 = (n as f64).sqrt().floor() as u32;
    if n < 1 { return false };
    for i in 2..=num {
        if n % i == 0 {
            return false;
        }
    }
    true
}

/*
//Threads
let x = 1;
let t1 = thread::spawn(move || {
println!("Hi from thread {}", x);
thread::sleep(Duration::from_millis(1));
});
t1.join().unwrap();
println!("{}", x);

//Message provider TX RX
let (tx, rx) = mpsc::channel(); //tx = transmitter , rx = receiever
let t_message = thread::spawn(move || {
let val = "Hi from t_message thread";
tx.send(val).unwrap(); //send message
});

let recieved = rx.recv().unwrap(); //get message
println!("Got: {}", recieved);
t_message.join().unwrap();



print!("{}",prime_check(23));*/