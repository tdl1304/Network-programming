use std::{thread, time};
use std::sync::{mpsc, Mutex, Arc};
use std::io::{stdin};
use std::time::{Instant};


fn main() {
    let (tx, rx) = mpsc::channel(); //tx = transmitter , rx = receieve
    let c = Arc::new(Mutex::new(0)); //counter
    let mut threads = vec![];
    let mut primes: Vec<u32> = vec![];
    let r: u32;

    let (lim, thread_count) = user_input();
    //ceil division
    if lim % thread_count == 0 {
        r = &lim/&thread_count;
    } else {
        r = &lim/&thread_count+1;
    }

    println!("Counting time for finding primes");
    let start = Instant::now();
    for i in 0..thread_count {
        let counter = Arc::clone(&c);
        let tx_clone = tx.clone();
        threads.push(thread::spawn(move || {
            let mut count: u32 = 0;
            for j in i*&r..(i+1)*&r {
                if j >= lim {
                    break;
                } else {
                    if prime_check(j) {
                        tx_clone.send(j).unwrap(); //send message
                        count += 1;
                    }
                }
            }
            *counter.lock().unwrap() += count;
        }));
    }


    for t in threads {
        t.join().unwrap();
    }

    let duration = start.elapsed();


    for it in rx.iter().take(*c.lock().unwrap() as usize) {
        primes.push(it);
    }

    primes.sort_unstable();
    for prime in primes {
        println!("{}", prime);
    }

    println!("Time elapsed is: {:#?}", duration);
    let millis = time::Duration::from_millis(5000);
    thread::sleep(millis);
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
    match s1.trim().parse::<u32>() {
        Ok(i) => a = i,
        Err(..) => println!("this was not an integer: {}", trimmed1),
    };
    match trimmed2.parse::<u32>() {
        Ok(i) => b = i,
        Err(..) => println!("this was not an integer: {}", trimmed2),
    };
    if a < b {
        panic!("Upper limit is greater than amount of threads");
    }
    (a, b)
}

fn prime_check(n:u32) -> bool {
    let num:u32 = (n as f64).sqrt().floor() as u32;
    if n <= 1 { return false };
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